using DiscussionService.Contracts;
using DiscussionService.Data;
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

        public async Task<IEnumerable<Question>> GetAllQuestionsAsync()
        {
            return await FindAll().ToListAsync();
        }

        public async Task<Question> GetQuestionByIdAsync(Guid questionId)
        {
            return await FindByCondition(question => question.Id.Equals(questionId)).FirstOrDefaultAsync();
        }

        public void UpdateQuestion(Question question)
        {
            Update(question);
        }
    }
}