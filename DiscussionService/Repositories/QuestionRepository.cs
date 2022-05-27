using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Dtos;
using DiscussionService.Helpers;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class QuestionRepository : RepositoryBase<Question>, IQuestionRepository
    {
        public QuestionRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public void CreateQuestion(Question question)
        {
            Create(question);
        }

        public void DeleteQuestion(Question question)
        {
            Delete(question);
        }

        public async Task<PagedList<Question>> GetAllQuestionsAsync(PaginationParams @params)
        {
            // return await FindByCondition(question => question.Removed == false)
            //                 .Skip((@params.Page - 1) * @params.ItemsPerPage)
            //                 .Take(@params.ItemsPerPage)
            //                 .ToListAsync();
            // return await FindAll()
            //                 .OrderBy(question => question.CreatedDate)
            //                 .Skip((@params.Page - 1) * @params.ItemsPerPage)
            //                 .Take(@params.ItemsPerPage)
            //                 .ToListAsync();
            var items = await FindAll().OrderBy(question => question.CreatedDate).ToListAsync();
            return PagedList<Question>.ToPagedList(items, @params.PageNumber, @params.PageSize);
        }

        public async Task<Question> GetQuestionByIdAsync(Guid questionId)
        {
            return await FindByCondition(question => question.Id.Equals(questionId))
                            .Include(_ => _.Answers.Where(answer => answer.Removed == false))
                            .FirstOrDefaultAsync();
            // return await FindByCondition(question => question.Id.Equals(questionId)).FirstOrDefaultAsync();
        }

        public void UpdateQuestion(Question question)
        {
            Update(question);
        }
    }
}