using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class AnswerRepository : RepositoryBase<Answer>, IAnswerRepository
    {
        public AnswerRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public void CreateAnswer(Answer answer)
        {
            Create(answer);
        }

        public void DeleteAnswer(Answer answer)
        {
            Delete(answer);
        }

        public async Task<IEnumerable<Answer>> GetAllAnswersAsync()
        {
            return await FindByCondition(answer => answer.Removed == false).ToListAsync();
        }

        public async Task<Answer> GetAnswerByIdAsync(Guid answerId)
        {
            return await FindByCondition(answer => answer.Id.Equals(answerId)).FirstOrDefaultAsync();

        }

        public void UpdateAnswer(Answer answer)
        {
            Update(answer);
        }
    }
}